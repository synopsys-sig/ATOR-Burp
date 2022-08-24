package burp;

public class FinalErrorCondition {
	public static String addErrorCondition() {
		String condition = "";
		try {
		String conditionname = (String) ReplacePanel.triggerConditionNameCombo.getSelectedItem();
		
		if(!conditionname.equals("NA")) {
			condition = conditionname;
		}
		
		for(MultipleErrorCondition mulCondition: ReplacePanel.multipleErrorConditions) {
			String name = (String) mulCondition.triggerComboBox.getSelectedItem();
			if(!name.equals("NA")) {
				String logical = (String) mulCondition.logicalCondition.getSelectedItem();
				condition += " " + logical + " " + name;
			}
		}
		
		return condition;
		}
		catch(Exception e) {
			BurpExtender.callbacks.printOutput("Exception in adding trigger condition"+ e.getLocalizedMessage());
		}
		return condition;
	}
}
