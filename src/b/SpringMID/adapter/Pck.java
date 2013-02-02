package b.SpringMID.adapter;

import java.util.ArrayList;
import java.util.List;

public class Pck implements Constants {
	public String id = null;
	public String name = null;
	public PckConverter converter = null;
	public int iTrim = 0;
	public String pre = null;
	public String post = null;
	public int iMaxLength = 0;
	public List<PckItem> items = new ArrayList<PckItem>();
}
