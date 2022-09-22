package burp;

public class ErrorEntry {
	public static int conditionentry = 0;
	
	private String conditionName, categoryName, value, description;
	public ErrorEntry(String conditionName, String categoryName, String value, String description) {
		if(conditionName == null) {
			conditionentry +=1 ;
			this.conditionName = "condition-"+ String.valueOf(conditionentry);
		}
		else {
			this.conditionName = conditionName;
		}
		
		this.categoryName = categoryName;
		this.value = value;
		this.description = description;
	}
	
	public String getConditionname() {
		return this.conditionName;
	}
	
	public String getCategory() {
		return this.categoryName;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getDescription() {
		return this.description;
	}
	
}
