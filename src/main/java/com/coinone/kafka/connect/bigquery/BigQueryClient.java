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

public class BigQueryClient implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(BigQueryClient.class);

    private static final Collection<String> SCOPES = Lists.newArrayList(
            "https://www.googleapis.com/auth/bigquery",
            "https://www.googleapis.com/auth/bigquery.insertdata",
            "https://www.googleapis.com/auth/cloud-platform"
    );

    private final String projectId;
    private final BigQueryWriteClient writeClient;
    private final BigQueryReadClient readClient;
    private final boolean useStorageApi;

    public BigQueryClient(String projectId, String keySource, String key, boolean useStorageApi)
            throws IOException {
        this.projectId = projectId;
        this.useStorageApi = useStorageApi;

        GoogleCredentials credentials = createCredentials(keySource, key);

        if (useStorageApi) {
            this.writeClient = createWriteClient(credentials);
            this.readClient = createReadClient(credentials);
        } else {
            this.writeClient = null;
            this.readClient = null;
        }
    }

    private GoogleCredentials createCredentials(String keySource, String key) throws IOException {
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
            throw new IllegalArgumentException("Invalid key source: " + keySource);
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        return useStorageApi ? credentials.createScoped(SCOPES) : credentials;
    }

    private BigQueryWriteClient createWriteClient(GoogleCredentials credentials) throws IOException {
        BigQueryWriteSettings writeSettings = BigQueryWriteSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .setQuotaProjectId(projectId)
                .build();

        return BigQueryWriteClient.create(writeSettings);
    }

    private BigQueryReadClient createReadClient(GoogleCredentials credentials) throws IOException {
        return BigQueryReadClient.create(
                BigQueryReadSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                        .setQuotaProjectId(projectId)
                        .build()
        );
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
    public void close() throws Exception {
        if (writeClient != null) {
            writeClient.close();
        }
        if (readClient != null) {
            readClient.close();
        }
    }
}
