package com.kaiser.messenger_server.modules.user.mbt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ModelState {
    private final Map<String, UserModel> users = new HashMap<>();

    public String randomUser() {

        if (users.isEmpty()) {
            return null;
        }

        List<String> ids = new ArrayList<>(users.keySet());

        int randomIndex = ThreadLocalRandom.current().nextInt(ids.size());

        return ids.get(randomIndex);
    }

    public boolean emailExists(String email) {
        return users.values()
            .stream()
            .anyMatch(user ->
                user.getEmail().equals(email)
            );
    }

    public void addUser(String id, UserModel user) {
        users.put(id, user);
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public UserModel getUser(String id) {
        return users.get(id);
    }

    public String getEmail(String id) {
        UserModel user = users.get(id);

        return user != null
            ? user.getEmail()
            : null;
    }

    public String randomEmail() {
        return users.values()
            .stream()
            .map(UserModel::getEmail)
            .findAny()
            .orElse(null);
    }

    public boolean userExists(String id) {
        return users.containsKey(id);
    }

    public int size() {
        return users.size();
    }

    public Map<String, UserModel> getUsers() {
        return users;
    }

    public void updateUser(String id, UserModel updatedUser) {
        if (!users.containsKey(id)) {
            return;
        }

        users.put(id, updatedUser);
    }

    public String prettyUsers() {
        if (users.isEmpty()) {
            return "[NO USERS]";
        }

        StringBuilder sb = new StringBuilder();

        users.forEach((id, user) -> {
            sb.append("\n");

            sb.append(" - ID          : ")
                .append(id)
                .append("\n");

            sb.append("   Username    : ")
                .append(user.getUsername())
                .append("\n");

            sb.append("   Email       : ")
                .append(user.getEmail())
                .append("\n");

            sb.append("   Image       : ")
                .append(user.getImage())
                .append("\n");

            sb.append("   AccountType : ")
                .append(user.getAccountType())
                .append("\n");

            sb.append("   Active      : ")
                .append(user.isActive())
                .append("\n");

            sb.append("   RoleId      : ")
                .append(user.getRoleId())
                .append("\n");
        });

        return sb.toString();
    }
}