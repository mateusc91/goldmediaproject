package com.globalmedia.videometadataservice.client;

import com.globalmedia.videometadataservice.domain.request.VideoCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j


public class ExternalVideoMockClient {

    private final Random random = new Random();
    private final String[] sources = {"YouTube", "Vimeo", "Internal"};
    private final String[] titles = {
            "Introduction to Spring Boot",
            "Advanced Java Programming",
            "Microservices Architecture",
            "Cloud Computing Fundamentals",
            "DevOps Best Practices",
            "Docker and Kubernetes",
            "React.js for Beginners",
            "Machine Learning Basics",
            "Data Science with Python",
            "Blockchain Technology"
    };

    private final String[] descriptions = {
            "Learn the basics of Spring Boot framework",
            "Advanced techniques for Java developers",
            "Understanding microservices architecture and implementation",
            "Introduction to cloud computing concepts",
            "Best practices for DevOps implementation",
            "Containerization with Docker and orchestration with Kubernetes",
            "Getting started with React.js for frontend development",
            "Introduction to machine learning algorithms",
            "Data analysis and visualization with Python",
            "Understanding blockchain technology and applications"
    };

    public List<VideoCreateRequest> fetchVideosFromExternalSource(String source, int count) {
        log.info("Fetching {} videos from {}", count, source);

        List<VideoCreateRequest> videos = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            VideoCreateRequest video = VideoCreateRequest.builder()
                    .title(getRandomTitle())
                    .description(getRandomDescription())
                    .url(generateRandomUrl(source))
                    .source(source)
                    .uploadDate(getRandomUploadDate())
                    .durationInSeconds(getRandomDuration())
                    .build();

            videos.add(video);
        }

        return videos;
    }

    public List<VideoCreateRequest> fetchVideosFromAllSources(int count) {
        List<VideoCreateRequest> allVideos = new ArrayList<>();

        for (String source : sources) {
            allVideos.addAll(fetchVideosFromExternalSource(source, count));
        }

        return allVideos;
    }

    public List<VideoCreateRequest> fetchVideos(String source, int count) {
        if (source != null) {
            return fetchVideosFromExternalSource(source, count);
        } else {
            return fetchVideosFromAllSources(count);
        }
    }

    private String getRandomTitle() {
        return titles[random.nextInt(titles.length)];
    }

    private String getRandomDescription() {
        return descriptions[random.nextInt(descriptions.length)];
    }

    private String generateRandomUrl(String source) {
        String baseUrl;
        switch (source) {
            case "YouTube":
                baseUrl = "https://youtube.com/watch?v=";
                break;
            case "Vimeo":
                baseUrl = "https://vimeo.com/";
                break;
            default:
                baseUrl = "https://internal.globalmedia.com/videos/";
                break;
        }

        return baseUrl + generateRandomId();
    }

    private String generateRandomId() {
        StringBuilder id = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < 11; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }

        return id.toString();
    }

    private LocalDate getRandomUploadDate() {
        long minDay = LocalDate.of(2020, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        long randomDay = minDay + random.nextInt((int) (maxDay - minDay));

        return LocalDate.ofEpochDay(randomDay);
    }

    private int getRandomDuration() {
        return random.nextInt(1800 - 30) + 30;
    }
}
