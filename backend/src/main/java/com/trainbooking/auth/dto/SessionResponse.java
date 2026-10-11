package com.trainbooking.auth.dto;

public record SessionResponse(boolean authenticated, UserProfileResponse user) {
}
