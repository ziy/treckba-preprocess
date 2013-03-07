package edu.cmu.cs.ziy.util;

import java.util.Calendar;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

public class DefaultPeriodicallyChangedObject<T> implements PeriodicallyChanged<T> {

  protected RangeMap<Calendar, T> period2value = TreeRangeMap.create();

  public DefaultPeriodicallyChangedObject() {
    super();
  }

  public DefaultPeriodicallyChangedObject(RangeMap<Calendar, T> period2value) {
    super();
    this.period2value = period2value;
  }

  @Override
  public void addValuePeriod(Range<Calendar> period, T value) {
    period2value.put(period, value);

  }

  @Override
  public T getValueAt(Calendar time) {
    return period2value.get(time);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((period2value == null) ? 0 : period2value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DefaultPeriodicallyChangedObject<?> other = (DefaultPeriodicallyChangedObject<?>) obj;
    if (period2value == null) {
      if (other.period2value != null)
        return false;
    } else if (!period2value.equals(other.period2value))
      return false;
    return true;
  }

}
