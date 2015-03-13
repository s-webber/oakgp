package org.oakgp.serialize;

import java.util.HashMap;
import java.util.Map;

import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Operator;
import org.oakgp.operator.Subtract;

public class SymbolMap {
	private static final Map<String, Class<? extends Operator>> SYMBOL_TO_CLASS_MAPPINGS = new HashMap<>();
	private static final Map<Class<? extends Operator>, String> CLASS_TO_SYMBOL_MAPPINGS = new HashMap<>();
	static {
		addMapping("+", Add.class);
		addMapping("-", Subtract.class);
		addMapping("*", Multiply.class);
	}

	private static void addMapping(String symbol, Class<? extends Operator> operatorClass) {
		SYMBOL_TO_CLASS_MAPPINGS.put(symbol, operatorClass);
		CLASS_TO_SYMBOL_MAPPINGS.put(operatorClass, symbol);
	}

	public String getDisplayName(Operator operator) {
		Class<? extends Operator> operatorClass = operator.getClass();
		String displayName = CLASS_TO_SYMBOL_MAPPINGS.get(operatorClass);
		if (displayName != null) {
			return displayName;
		} else {
			return operatorClass.getName();
		}
	}

	public Operator getOperator(String symbol) {
		Class<? extends Operator> operatorClass = SYMBOL_TO_CLASS_MAPPINGS.get(symbol);
		if (operatorClass == null) {
			operatorClass = findClass(symbol);
		}
		return newInstance(operatorClass);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Operator> findClass(String className) {
		try {
			return (Class<? extends Operator>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Could not find class: " + className);
		}
	}

	private Operator newInstance(Class<? extends Operator> operatorClass) {
		try {
			return operatorClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Could not create new instance of class: " + operatorClass);
		}
	}
}
