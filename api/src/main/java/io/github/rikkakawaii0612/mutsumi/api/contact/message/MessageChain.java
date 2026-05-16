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

    @Override
    public void visit(Visitor visitor) {
        this.components.forEach(visitor::accept);
    }

    @Override
    public String asString() {
        StringBuilder builder = new StringBuilder();
        this.visit(message -> builder.append(message.asString()));
        return builder.toString();
    }

    public static MessageChain of(Message... messages) {
        List<SingleMessage> list = new ArrayList<>();
        Text text = null;
        for (Message message : messages) {
            if (message instanceof Text t) {
                text = text == null ? t : Message.text(text.content() + t.content());
            } else {
                if (text != null) {
                    list.add(text);
                    text = null;
                }
                message.visit(list::add);
            }
        }
        if (text != null) {
            list.add(text);
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
