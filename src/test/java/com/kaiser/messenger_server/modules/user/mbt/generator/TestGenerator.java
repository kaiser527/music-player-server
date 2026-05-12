package com.kaiser.messenger_server.modules.user.mbt.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.kaiser.messenger_server.enums.AccountType;
import com.kaiser.messenger_server.modules.user.mbt.model.ModelState;
import com.kaiser.messenger_server.modules.user.mbt.model.UserModel;
import com.kaiser.messenger_server.modules.user.mbt.state.ActionState;

public class TestGenerator {
    private static final Random random = new Random();

    public static List<ActionState> generateSequence(int length, ModelState model) {
        List<ActionState> sequence = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            ActionState action;

            if (model.size() == 0) {
                action = ActionState.CREATE_USER;
            } else {
                action = randomAction();
            }

            sequence.add(action);

            simulate(action, model);
        }

        return sequence;
    }

    private static ActionState randomAction() {
        ActionState[] actions = ActionState.values();

        return actions[random.nextInt(actions.length)];
    }

    private static void simulate(ActionState action, ModelState model) {
        switch (action) {
            case CREATE_USER:
                int rand = random.nextInt(1000);

                UserModel user =
                    UserModel.builder()
                        .username("temp_user_" + rand)
                        .email("temp_" + rand + "@mail.com")
                        .image("default.png")
                        .accountType(AccountType.LOCAL)
                        .active(true)
                        .roleId("USER")
                        .build();

                model.addUser(
                    TestDataGenerator.randomUserId(),
                    user
                );

                break;

            case DELETE_USER:
                String id = model.randomUser();

                if (id != null) {
                    model.removeUser(id);
                }

                break;

            case UPDATE_USER:
                String updateId =
                    model.randomUser();

                if (updateId != null) {

                    UserModel oldUser = model.getUser(updateId);

                    UserModel updatedUser =
                        UserModel.builder()
                            .username(
                                oldUser.getUsername() + "_updated"
                            )
                            .email(
                                oldUser.getEmail()
                            )
                            .image("updated.png")
                            .accountType(AccountType.GOOGLE)
                            .active(true)
                            .roleId(oldUser.getRoleId())
                            .build();

                    model.updateUser(updateId, updatedUser);
                }

                break;
        }
    }
}