package br.com.while42.argentum.model;

import java.util.Calendar;
import java.util.List;

public class CandleStickFactory {
	public Candlestick buidCandleToDate(Calendar date,
			List<Operation> operations) {

		double min = operations.get(0).getValue();
		double max = operations.get(0).getValue();
		
		double volume = 0.0;
		
		for (Operation op: operations) {
			volume += op.getVolume();
			
			if (op.getValue() > max) {
				max = op.getValue();
			} else if (op.getValue() < min) {
				min = op.getValue();
			}			
		}		

		double first = operations.get(0).getValue();
		double close = operations.get(operations.size() - 1).getValue();

		return new Candlestick(first, close, min, max, volume, date);
	}
}
