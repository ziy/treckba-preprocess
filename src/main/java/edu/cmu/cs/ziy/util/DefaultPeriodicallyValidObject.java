package edu.cmu.cs.ziy.util;

import java.io.Serializable;
import java.util.Calendar;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class DefaultPeriodicallyValidObject implements PeriodicallyValid, Serializable {

  private static final long serialVersionUID = 1L;

  protected RangeSet<Calendar> periods = TreeRangeSet.create();

  public DefaultPeriodicallyValidObject() {
    super();
  }

  public DefaultPeriodicallyValidObject(RangeSet<Calendar> periods) {
    super();
    this.periods = periods;
  }

  @Override
  public void addValidPeriod(Range<Calendar> period) {
    periods.add(period);
  }

  @Override
  public boolean isValidAt(Calendar time) {
    return periods.contains(time);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((periods == null) ? 0 : periods.hashCode());
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
    DefaultPeriodicallyValidObject other = (DefaultPeriodicallyValidObject) obj;
    if (periods == null) {
      if (other.periods != null)
        return false;
    } else if (!periods.equals(other.periods))
      return false;
    return true;
  }

}
