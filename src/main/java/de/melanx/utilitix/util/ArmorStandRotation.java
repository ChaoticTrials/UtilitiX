package de.melanx.utilitix.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Rotations;

public class ArmorStandRotation {

    public final Rotations head;
    public final Rotations body;
    public final Rotations leftArm;
    public final Rotations rightArm;
    public final Rotations leftLeg;
    public final Rotations rightLeg;

    public ArmorStandRotation(Rotations head, Rotations body, Rotations leftArm, Rotations rightArm, Rotations leftLeg, Rotations rightLeg) {
        this.head = head;
        this.body = body;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }

    public static ArmorStandRotation defaultRotation() {
        return new ArmorStandRotation(
                ArmorStandEntity.DEFAULT_HEAD_ROTATION,
                ArmorStandEntity.DEFAULT_BODY_ROTATION,
                ArmorStandEntity.DEFAULT_LEFTARM_ROTATION,
                ArmorStandEntity.DEFAULT_RIGHTARM_ROTATION,
                ArmorStandEntity.DEFAULT_LEFTLEG_ROTATION,
                ArmorStandEntity.DEFAULT_RIGHTLEG_ROTATION
        );
    }
    
    public static ArmorStandRotation create(
            float headX, float headY, float headZ,
            float bodyX, float bodyY, float bodyZ,
            float leftArmX, float leftArmY, float leftArmZ,
            float rightArmX, float rightArmY, float rightArmZ,
            float leftLegX, float leftLegY, float leftLegZ,
            float rightLegX, float rightLegY, float rightLegZ
    ) {
        return new ArmorStandRotation(
                new Rotations(headX, headY, headZ),
                new Rotations(bodyX, bodyY, bodyZ),
                new Rotations(leftArmX, leftArmY, leftArmZ),
                new Rotations(rightArmX, rightArmY, rightArmZ),
                new Rotations(leftLegX, leftLegY, leftLegZ),
                new Rotations(rightLegX, rightLegY, rightLegZ)
        );
    }
    
    public void write(PacketBuffer buffer) {
        buffer.writeFloat(this.head.getX());
        buffer.writeFloat(this.head.getY());
        buffer.writeFloat(this.head.getZ());
        buffer.writeFloat(this.body.getX());
        buffer.writeFloat(this.body.getY());
        buffer.writeFloat(this.body.getZ());
        buffer.writeFloat(this.leftArm.getX());
        buffer.writeFloat(this.leftArm.getY());
        buffer.writeFloat(this.leftArm.getZ());
        buffer.writeFloat(this.rightArm.getX());
        buffer.writeFloat(this.rightArm.getY());
        buffer.writeFloat(this.rightArm.getZ());
        buffer.writeFloat(this.leftLeg.getX());
        buffer.writeFloat(this.leftLeg.getY());
        buffer.writeFloat(this.leftLeg.getZ());
        buffer.writeFloat(this.rightLeg.getX());
        buffer.writeFloat(this.rightLeg.getY());
        buffer.writeFloat(this.rightLeg.getZ());
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.add("head", rot(this.head));
        json.add("body", rot(this.body));
        json.add("left_arm", rot(this.leftArm));
        json.add("right_arm", rot(this.rightArm));
        json.add("left_leg", rot(this.leftLeg));
        json.add("right_leg", rot(this.rightLeg));
        return json;
    }

    private static JsonArray rot(Rotations rot) {
        JsonArray array = new JsonArray();
        array.add(rot.getX());
        array.add(rot.getY());
        array.add(rot.getZ());
        return array;
    }

    public void apply(ArmorStandEntity entity) {
        entity.setHeadRotation(this.head);
        entity.setBodyRotation(this.body);
        entity.setLeftArmRotation(this.leftArm);
        entity.setRightArmRotation(this.rightArm);
        entity.setLeftLegRotation(this.leftLeg);
        entity.setRightLegRotation(this.rightLeg);
    }

    public static ArmorStandRotation read(PacketBuffer buffer) {
        return new ArmorStandRotation(
                new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                new Rotations(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
        );
    }

    public static ArmorStandRotation deserialize(JsonObject json) {
        return new ArmorStandRotation(
                rot(json, "head"),
                rot(json, "body"),
                rot(json, "left_arm"),
                rot(json, "right_arm"),
                rot(json, "left_leg"),
                rot(json, "right_leg")
        );
    }

    private static Rotations rot(JsonObject json, String key) {
        JsonArray array = json.getAsJsonArray(key);
        if (array.size() != 3) {
            throw new IllegalStateException("Invalid rotation: List must have a length of 3.");
        }
        return new Rotations(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }
}
