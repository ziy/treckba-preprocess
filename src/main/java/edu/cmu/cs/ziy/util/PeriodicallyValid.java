package edu.cmu.cs.ziy.util;

import java.util.Calendar;

import com.google.common.collect.Range;

public interface PeriodicallyValid {

  void addValidPeriod(Range<Calendar> period);

  boolean isValidAt(Calendar time);
}
