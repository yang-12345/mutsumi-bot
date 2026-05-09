package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public interface Message {
    String asString();

    void visit(Visitor visitor);

    default MessageChain append(Message message) {
        return MessageChain.of(this, message);
    }

    default MessageChain append(String text) {
        return this.append(text(text));
    }

    static Text text(String content) {
        return new Text(content);
    }

    static At at(long target) {
        return new At(target);
    }

    static ByteArrayImage image(byte[] data) {
        return new ByteArrayImage(data);
    }

    interface Visitor {
        void accept(SingleMessage message);
    }
}
