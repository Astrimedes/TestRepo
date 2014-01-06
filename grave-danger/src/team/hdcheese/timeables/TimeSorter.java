package team.hdcheese.timeables;

import java.util.Comparator;

public class TimeSorter implements Comparator<Timeable> {

	public TimeSorter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Timeable t1, Timeable t2) {
		
		float time1 = t1.getAbsoluteTime();
		float time2 = t2.getAbsoluteTime();
		
		if (time1 < time2) {
			return -1;
		} else if (time1 > time2) {
			return 1;
		} else {
			return 0;
		}
	}

}
