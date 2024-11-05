package com.coinone.kafka.connect.bigquery;

import com.coinone.kafka.connect.bigquery.exception.BigQueryClientException;
import com.coinone.kafka.connect.bigquery.exception.BigQueryCredentialsException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.storage.v1.BigQueryReadClient;
import com.google.cloud.bigquery.storage.v1.BigQueryReadSettings;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BigQueryClientTest {

    private static final String PROJECT_ID = "test-project";
    private static final String JSON_KEY = "{\"type\": \"service_account\"}";
    
    @Mock
    private GoogleCredentials credentials;
    
    @Mock
    private BigQueryWriteClient writeClient;
    
    @Mock
    private BigQueryReadClient readClient;

    @Test
    void shouldCreateClientWithJsonKey() throws IOException {
        try (MockedStatic<GoogleCredentials> googleCredentials = mockStatic(GoogleCredentials.class)) {
            // Given
            GoogleCredentials scopedCredentials = mock(GoogleCredentials.class);
            when(credentials.createScoped(any(Collection.class))).thenReturn(scopedCredentials);
            googleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(credentials);
            
            try (MockedStatic<BigQueryWriteClient> writeClientStatic = mockStatic(BigQueryWriteClient.class);
                 MockedStatic<BigQueryReadClient> readClientStatic = mockStatic(BigQueryReadClient.class)) {
                
                writeClientStatic.when(() -> BigQueryWriteClient.create(any(BigQueryWriteSettings.class))).thenReturn(writeClient);
                readClientStatic.when(() -> BigQueryReadClient.create(any(BigQueryReadSettings.class))).thenReturn(readClient);

                // When
                try (BigQueryClient client = new BigQueryClient(PROJECT_ID, "JSON", JSON_KEY, true)) {
                    // Then
                    assertThat(client.getWriteClient()).isNotNull();
                    assertThat(client.getReadClient()).isNotNull();
                    assertThat(client.getProjectId()).isEqualTo(PROJECT_ID);
                    assertThat(client.isUseStorageApi()).isTrue();
                    verify(credentials).createScoped(any(Collection.class));
                }
            }
        }
    }

    @Test
    void shouldCreateClientWithFileKey() throws IOException {
        // Given
        Path tempFile = Files.createTempFile("credentials", ".json");
        Files.write(tempFile, JSON_KEY.getBytes());

        try (MockedStatic<GoogleCredentials> googleCredentials = mockStatic(GoogleCredentials.class)) {
            GoogleCredentials scopedCredentials = mock(GoogleCredentials.class);
            when(credentials.createScoped(any(Collection.class))).thenReturn(scopedCredentials);
            googleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(credentials);
            
            try (MockedStatic<BigQueryWriteClient> writeClientStatic = mockStatic(BigQueryWriteClient.class);
                 MockedStatic<BigQueryReadClient> readClientStatic = mockStatic(BigQueryReadClient.class)) {
                
                writeClientStatic.when(() -> BigQueryWriteClient.create(any(BigQueryWriteSettings.class))).thenReturn(writeClient);
                readClientStatic.when(() -> BigQueryReadClient.create(any(BigQueryReadSettings.class))).thenReturn(readClient);

                // When
                try (BigQueryClient client = new BigQueryClient(PROJECT_ID, "FILE", tempFile.toString(), true)) {
                    // Then
                    assertThat(client.getWriteClient()).isNotNull();
                    assertThat(client.getReadClient()).isNotNull();
                    verify(credentials).createScoped(any(Collection.class));
                }
            }
        }

        Files.delete(tempFile);
    }

    @Test
    void shouldCreateClientWithApplicationDefaultCredentials() throws IOException {
        try (MockedStatic<GoogleCredentials> googleCredentials = mockStatic(GoogleCredentials.class);
             BigQueryClient client = new BigQueryClient(PROJECT_ID, "APPLICATION_DEFAULT", null, false)) {
            
            // Given
            googleCredentials.when(GoogleCredentials::getApplicationDefault).thenReturn(credentials);

            // Then
            assertThat(client.getWriteClient()).isNull();
            assertThat(client.getReadClient()).isNull();
            assertThat(client.isUseStorageApi()).isFalse();
        }
    }

    @Test
    void shouldThrowExceptionForInvalidKeySource() {
        assertThatThrownBy(() -> 
            new BigQueryClient(PROJECT_ID, "INVALID", "key", true)
        )
            .isInstanceOf(BigQueryCredentialsException.class)
            .hasMessageContaining("Invalid key source");
    }

    @Test
    void shouldCloseClientsSuccessfully() throws Exception {
        try (MockedStatic<GoogleCredentials> googleCredentials = mockStatic(GoogleCredentials.class)) {
            // Given
            googleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(credentials);
            
            try (MockedStatic<BigQueryWriteClient> writeClientStatic = mockStatic(BigQueryWriteClient.class);
                 MockedStatic<BigQueryReadClient> readClientStatic = mockStatic(BigQueryReadClient.class)) {
                
                writeClientStatic.when(() -> BigQueryWriteClient.create(any(BigQueryWriteSettings.class))).thenReturn(writeClient);
                readClientStatic.when(() -> BigQueryReadClient.create(any(BigQueryReadSettings.class))).thenReturn(readClient);

                // When
                BigQueryClient client = new BigQueryClient(PROJECT_ID, "JSON", JSON_KEY, true);
                client.close();

                // Then
                verify(writeClient).close();
                verify(readClient).close();
            }
        }
    }

    @Test
    void shouldThrowExceptionWhenCloseFails() throws Exception {
        try (MockedStatic<GoogleCredentials> googleCredentials = mockStatic(GoogleCredentials.class)) {
            // Given
            googleCredentials.when(() -> GoogleCredentials.fromStream(any())).thenReturn(credentials);
            
            try (MockedStatic<BigQueryWriteClient> writeClientStatic = mockStatic(BigQueryWriteClient.class);
                 MockedStatic<BigQueryReadClient> readClientStatic = mockStatic(BigQueryReadClient.class)) {
                
                writeClientStatic.when(() -> BigQueryWriteClient.create(any(BigQueryWriteSettings.class))).thenReturn(writeClient);
                readClientStatic.when(() -> BigQueryReadClient.create(any(BigQueryReadSettings.class))).thenReturn(readClient);

                doThrow(new IOException("Failed to close")).when(writeClient).close();

                // When
                BigQueryClient client = new BigQueryClient(PROJECT_ID, "JSON", JSON_KEY, true);

                // Then
                assertThatThrownBy(client::close)
                    .isInstanceOf(BigQueryClientException.class)
                    .hasMessageContaining("Failed to close BigQuery clients");
            }
        }
    }
} 