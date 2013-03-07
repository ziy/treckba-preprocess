package edu.cmu.cs.ziy.util;

import java.util.Calendar;

import com.google.common.collect.Range;

public interface PeriodicallyChanged<T> {

  void addValuePeriod(Range<Calendar> period, T value);

  T getValueAt(Calendar time);
}
