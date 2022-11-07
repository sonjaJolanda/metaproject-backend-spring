package main.java.org.htwg.konstanz.metaproject.rights;

public class UserRight {

	private Rights right;

	private Long elementId;

	private String elementType;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRight other = (UserRight) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (elementType != other.elementType)
			return false;
		if (right != other.right)
			return false;
		return true;
	}

	public Rights getRight() {
		return right;
	}

	public void setRight(Rights right) {
		this.right = right;
	}

	public Long getElementId() {
		return elementId;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

}
