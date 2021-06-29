package burp;

import javax.swing.JComboBox;

public class MultipleErrorCondition {
	IBurpExtenderCallbacks callbacks;
	JComboBox<String> logicalCondition;
	JComboBox<String> triggerComboBox;
	public MultipleErrorCondition(IBurpExtenderCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	
}
