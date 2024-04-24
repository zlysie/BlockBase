package net.oikmo.engine.models.entity;

public class EntityModel {
	
	private ModelPart head;
	private ModelPart torso;
	private ModelPart leftArm;
	private ModelPart rightArm;
	private ModelPart leftLeg;
	private ModelPart rightLeg;
	
	public EntityModel(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm, ModelPart leftLeg, ModelPart rightLeg) {
		this.head = head;
		this.torso = torso;
		this.leftArm = leftArm;
		this.rightArm = rightArm;
		this.leftLeg = leftLeg;
		this.rightLeg = rightLeg;
	}

	public ModelPart getHead() {
		return head;
	}

	public ModelPart getTorso() {
		return torso;
	}

	public ModelPart getLeftArm() {
		return leftArm;
	}

	public ModelPart getRightArm() {
		return rightArm;
	}

	public ModelPart getLeftLeg() {
		return leftLeg;
	}

	public ModelPart getRightLeg() {
		return rightLeg;
	}
}
