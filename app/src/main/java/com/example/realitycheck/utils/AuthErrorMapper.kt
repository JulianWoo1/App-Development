package com.example.realitycheck.utils

import android.util.Log

object AuthErrorMapper {

    private const val TAG = "AuthRepository"

    fun map(exception: Throwable): Exception {
        Log.e(TAG, "Authentication error", exception)

        val message = (exception.message + (exception.cause?.message ?: "")).lowercase()

        return Exception(
            when {
                "invalid login credentials" in message ||
                        "invalid email or password" in message ||
                        "email not found" in message ->
                    "Incorrect email or password."

                "email not confirmed" in message ||
                        "email link is invalid or has expired" in message ->
                    "Please verify your email before logging in."

                "user already registered" in message ||
                        "email address is already" in message ->
                    "An account with this email already exists."

                "password should be at least" in message ->
                    "Password must be at least 6 characters."

                "rate limit" in message ||
                        "too many requests" in message ->
                    "Too many attempts. Please wait a moment and try again."

                "network" in message ||
                        "unable to resolve host" in message ||
                        "failed to connect" in message ||
                        "timeout" in message ->
                    "No internet connection. Please try again."

                "jwt" in message ||
                        "session" in message ||
                        "token" in message ->
                    "Your session has expired. Please sign in again."

                "user not found" in message ->
                    "No account found with this email."

                "unable to validate email" in message ||
                        "invalid format" in message ->
                    "Please enter a valid email address."

                else ->
                    "Something went wrong. Please try again."

            }
        )
    }

    fun log(operation: String, exception: Throwable) {
        Log.e(TAG, "$operation failed", exception)
    }
}