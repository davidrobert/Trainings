package br.com.while42.argentum.model;

import java.util.Calendar;
import junit.framework.Assert;
import org.junit.Test;

public class TradeTest {

	@Test
	public void imutableDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 15);

		Trade trade = new Trade(10, 5, calendar);
		trade.getDate().set(Calendar.DAY_OF_MONTH, 20);

		Assert.assertEquals(15, trade.getDate().get(Calendar.DAY_OF_MONTH));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void TradeWithNullDate() {
		new Trade(10, 5, null);		
	}
	
	@Test
	public void isSameDay() {
		Calendar hoje = Calendar.getInstance();		
		Trade t1 = new Trade(40.5, 100, hoje);
		
		Assert.assertTrue(t1.isSameDay(hoje));
	}
	
	@Test
	public void isDiferentDay() {
		Calendar hoje = Calendar.getInstance();		
		Trade t1 = new Trade(40.5, 100, hoje);
		
		hoje.set(Calendar.DAY_OF_MONTH, 15);
		
		Assert.assertTrue(t1.isSameDay(hoje));
	}
}
