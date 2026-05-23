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

    default MessageChain append(char text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(boolean text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(byte text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(short text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(int text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(long text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(float text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(double text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain append(Object text) {
        return this.append(String.valueOf(text));
    }

    default MessageChain appendAutonym() {
        return this.append(new Autonym());
    }

    static Text text(String content) {
        return new Text(content);
    }

    static At at(long target) {
        return new At(target);
    }

    /**
     * 自动添加一个空格的 at.
     */
    static MessageChain atThen(long target) {
        return at(target).append(" ");
    }

    static ByteArrayImage image(byte[] data) {
        return new ByteArrayImage(data);
    }

    interface Visitor {
        void accept(SingleMessage message);
    }
}
