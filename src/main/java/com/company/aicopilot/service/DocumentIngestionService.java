package com.company.aicopilot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    private final ResourceLoader resourceLoader;

    public DocumentIngestionService(
            VectorStore vectorStore,
            ResourceLoader resourceLoader) {

        this.vectorStore = vectorStore;
        this.resourceLoader = resourceLoader;
    }

    public void ingestDatabaseIncident() {

        Resource resource = resourceLoader.getResource(
                "classpath:knowledge/database-connection-pool.md");

        TextReader reader = new TextReader(resource);

        List<Document> documents = reader.get();

        documents.forEach(document ->
                document.getMetadata().put(
                        "source",
                        "database-connection-pool.md"
                )
        );

        TokenTextSplitter splitter = new TokenTextSplitter();

        List<Document> chunks = splitter.apply(documents);

        vectorStore.add(chunks);
    }
}