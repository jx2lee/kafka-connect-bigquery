package com.coinone.kafka.connect.bigquery;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.storage.v1.BigQueryReadClient;
import com.google.cloud.bigquery.storage.v1.BigQueryReadSettings;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

import com.coinone.kafka.connect.bigquery.exception.BigQueryClientException;
import com.coinone.kafka.connect.bigquery.exception.BigQueryCredentialsException;

public class BigQueryClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(BigQueryClient.class);

    private static final Collection<String> WRITE_SCOPES = Lists.newArrayList(
            "https://www.googleapis.com/auth/bigquery.insertdata"
    );

    private static final Collection<String> READ_SCOPES = Lists.newArrayList(
            "https://www.googleapis.com/auth/bigquery.readonly"
    );

    private final String projectId;
    private final BigQueryWriteClient writeClient;
    private final BigQueryReadClient readClient;
    private final boolean useStorageApi;

    public BigQueryClient(String projectId, String keySource, String key, boolean useStorageApi) {
        this.projectId = projectId;
        this.useStorageApi = useStorageApi;

        try {
            GoogleCredentials credentials = createCredentials(keySource, key);

            if (useStorageApi) {
                this.writeClient = createWriteClient(credentials);
                this.readClient = createReadClient(credentials);
            } else {
                this.writeClient = null;
                this.readClient = null;
            }
        } catch (IOException e) {
            throw new BigQueryClientException("Failed to initialize BigQuery client", e);
        }
    }

    private GoogleCredentials createCredentials(String keySource, String key) throws IOException {
        try {
            if (key == null && "APPLICATION_DEFAULT".equals(keySource)) {
                logger.debug("Using application default credentials");
                return GoogleCredentials.getApplicationDefault();
            }

            InputStream credentialsStream;
            if ("JSON".equals(keySource)) {
                credentialsStream = new ByteArrayInputStream(Objects.requireNonNull(key).getBytes(StandardCharsets.UTF_8));
            } else if ("FILE".equals(keySource)) {
                logger.debug("Loading credentials from file: {}", key);
                credentialsStream = new FileInputStream(Objects.requireNonNull(key));
            } else {
                throw new BigQueryCredentialsException("Invalid key source: " + keySource);
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            if (!useStorageApi) {
                return credentials;
            }
            
            Collection<String> requiredScopes = new ArrayList<>();
            if (writeClient != null) {
                requiredScopes.addAll(WRITE_SCOPES);
            }
            if (readClient != null) {
                requiredScopes.addAll(READ_SCOPES);
            }
            
            return credentials.createScoped(requiredScopes);
        } catch (IOException e) {
            throw new BigQueryCredentialsException("Failed to create credentials", e);
        }
    }

    private BigQueryWriteClient createWriteClient(GoogleCredentials credentials) {
        try {
            BigQueryWriteSettings writeSettings = BigQueryWriteSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .setQuotaProjectId(projectId)
                    .build();

            return BigQueryWriteClient.create(writeSettings);
        } catch (IOException e) {
            throw new BigQueryClientException("Failed to create BigQuery write client", e);
        }
    }

    private BigQueryReadClient createReadClient(GoogleCredentials credentials) {
        try {
            return BigQueryReadClient.create(
                    BigQueryReadSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                            .setQuotaProjectId(projectId)
                            .build()
            );
        } catch (IOException e) {
            throw new BigQueryClientException("Failed to create BigQuery read client", e);
        }
    }

    public BigQueryWriteClient getWriteClient() {
        return writeClient;
    }

    public BigQueryReadClient getReadClient() {
        return readClient;
    }

    public boolean isUseStorageApi() {
        return useStorageApi;
    }

    public String getProjectId() {
        return projectId;
    }

    @Override
    public void close() {
        try {
            if (writeClient != null) {
                writeClient.close();
            }
            if (readClient != null) {
                readClient.close();
            }
        } catch (Exception e) {
            throw new BigQueryClientException("Failed to close BigQuery clients", e);
        }
    }
}
