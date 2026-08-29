package de.melanx.utilitix.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Rotations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.decoration.ArmorStand;

@SuppressWarnings("ClassCanBeRecord")
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
                ArmorStand.DEFAULT_HEAD_POSE,
                ArmorStand.DEFAULT_BODY_POSE,
                ArmorStand.DEFAULT_LEFT_ARM_POSE,
                ArmorStand.DEFAULT_RIGHT_ARM_POSE,
                ArmorStand.DEFAULT_LEFT_LEG_POSE,
                ArmorStand.DEFAULT_RIGHT_LEG_POSE
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

    public void write(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.head.x());
        buffer.writeFloat(this.head.y());
        buffer.writeFloat(this.head.z());
        buffer.writeFloat(this.body.x());
        buffer.writeFloat(this.body.y());
        buffer.writeFloat(this.body.z());
        buffer.writeFloat(this.leftArm.x());
        buffer.writeFloat(this.leftArm.y());
        buffer.writeFloat(this.leftArm.z());
        buffer.writeFloat(this.rightArm.x());
        buffer.writeFloat(this.rightArm.y());
        buffer.writeFloat(this.rightArm.z());
        buffer.writeFloat(this.leftLeg.x());
        buffer.writeFloat(this.leftLeg.y());
        buffer.writeFloat(this.leftLeg.z());
        buffer.writeFloat(this.rightLeg.x());
        buffer.writeFloat(this.rightLeg.y());
        buffer.writeFloat(this.rightLeg.z());
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
        array.add(rot.x());
        array.add(rot.y());
        array.add(rot.z());
        return array;
    }

    public void apply(ArmorStand entity) {
        entity.setHeadPose(this.head);
        entity.setBodyPose(this.body);
        entity.setLeftArmPose(this.leftArm);
        entity.setRightArmPose(this.rightArm);
        entity.setLeftLegPose(this.leftLeg);
        entity.setRightLegPose(this.rightLeg);
    }

    public static ArmorStandRotation read(FriendlyByteBuf buffer) {
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
