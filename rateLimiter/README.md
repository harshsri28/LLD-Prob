# Rate Limiter Implementation

This project implements a Rate Limiter system in Java, featuring four different rate limiting strategies. A Rate Limiter is a tool that monitors the number of requests per unit of time and blocks requests if the limit is exceeded.

## Problem Statement

The goal is to design and implement a flexible Rate Limiter that can support multiple strategies to control the rate of traffic sent by a client or service. The system should be thread-safe and scalable.

## Strategies Implemented

### 1. Token Bucket
- **Description**: This algorithm maintains a bucket of tokens. Tokens are added at a constant rate (refill rate). When a request comes in, it attempts to consume a token. If a token is available, the request is allowed. If not, it is denied.
- **Key Features**: Allows for bursts of traffic up to the bucket capacity.
- **Implementation**: `TokenBucketRateLimiter.java`

### 2. Leaky Bucket
- **Description**: Similar to a bucket with a hole in the bottom. Requests enter the bucket and "leak" out at a constant rate. If the bucket overflows (i.e., incoming rate > leak rate for a sustained period), requests are discarded.
- **Key Features**: Smooths out bursts, enforcing a constant output rate.
- **Implementation**: `LeakyBucketRateLimiter.java`

### 3. Fixed Window Counter
- **Description**: The timeline is divided into fixed windows (e.g., 1 minute). A counter is maintained for each window. If the counter exceeds the limit, requests are dropped until the next window starts.
- **Key Features**: Simple to implement.
- **Drawback**: Can allow 2x the limit at the boundary of windows (e.g., all requests at the end of minute 1 and start of minute 2).
- **Implementation**: `FixedWindowRateLimiter.java`

### 4. Sliding Window Log
- **Description**: Keeps a log of timestamps for each request. To check if a request is allowed, it looks at the logs and counts how many requests fall within the window ending at the current time. Old timestamps are removed.
- **Key Features**: Very accurate, solves the boundary issue of Fixed Window.
- **Drawback**: High memory footprint as it stores timestamps for every request in the window.
- **Implementation**: `SlidingWindowRateLimiter.java`

## Usage

The system uses a `RateLimiterManager` to manage the active rate limiter strategy.

```java
RateLimiterManager instance = RateLimiterManager.getInstance();

// Default strategy is TokenBucket (or configured otherwise)
boolean allowed = instance.allowedRequest("client1");

// Switch strategy dynamically
instance.updateRateLimiter("sliding", 20, 60_000); // 20 requests per 60 seconds
```

## Structure

- `src/main/java/org/example/strategy/`: Contains the `RateLimiter` interface and concrete implementations.
- `src/main/java/org/example/service/`: Contains the `RateLimiterManager` singleton.
- `src/main/java/org/example/factory/`: Contains the `RateLimiterFactory`.
