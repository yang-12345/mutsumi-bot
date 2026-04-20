package io.github.rikkakawaii0612.mutsumi.api.contact.message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MessageChain implements Message {
    public static final MessageChain EMPTY = new MessageChain(List.of());
    private final List<SingleMessage> components;

    MessageChain(List<SingleMessage> components) {
        this.components = components;
    }

    public void forEach(Consumer<Message> consumer) {
        this.visit(consumer::accept);
    }

    @Override
    public void visit(Visitor visitor) {
        this.components.forEach(visitor::accept);
    }

    @Override
    public String asString() {
        StringBuilder builder = new StringBuilder();
        this.forEach(message -> builder.append(message.asString()));
        return builder.toString();
    }

    public static MessageChain of(Message... messages) {
        List<SingleMessage> list = new ArrayList<>();
        for (Message message : messages) {
            message.visit(list::add);
        }
        return list.isEmpty() ? EMPTY : new MessageChain(List.copyOf(list));
    }

    public static class Builder {
        List<Message> messages = new ArrayList<>();

        public Builder append(Message message) {
            this.messages.add(message);
            return this;
        }

        public MessageChain build() {
            return of(this.messages.toArray(new Message[0]));
        }
    }
}
