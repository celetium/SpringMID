package b.SpringMID.business.test;

import b.SpringMID.business.Tx;
import b.SpringMID.business.TxStep;
import b.SpringMID.business.Txlet;

public class TxA01M01 implements Txlet {

	@Override
	public void run(Tx t) {
		System.out.println("Txlet: ");
		TxStep step = new TxStep(t);
		step.getRequest().set("HI", "MAN");
		step.call();
		step.getResponse().get("HEY");
	}

}
