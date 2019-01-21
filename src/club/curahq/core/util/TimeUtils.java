package club.curahq.core.util;

import java.text.DecimalFormat; 
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.DurationFormatUtils;

public class TimeUtils
{
  public static DecimalFormat getDecimalFormat()
  {
    return new DecimalFormat("0.0");
  }
  
  public static long parse(String input)
  {
    if ((input == null) || (input.isEmpty())) {
      return -1L;
    }
    long result = 0L;
    StringBuilder number = new StringBuilder();
    for (int i = 0; i < input.length(); i++)
    {
      char c = input.charAt(i);
      if (Character.isDigit(c))
      {
        number.append(c);
      }
      else
      {
        String str;
        if ((Character.isLetter(c)) && (!(str = number.toString()).isEmpty()))
        {
          result += convert(Integer.parseInt(str), c);
          number = new StringBuilder();
        }
      }
    }
    return result;
  }
  
  private static long convert(int value, char unit)
  {
    switch (unit)
    {
    case 'y': 
      return value * TimeUnit.DAYS.toMillis(365L);
    case 'M': 
      return value * TimeUnit.DAYS.toMillis(30L);
    case 'd': 
      return value * TimeUnit.DAYS.toMillis(1L);
    case 'h': 
      return value * TimeUnit.HOURS.toMillis(1L);
    case 'm': 
      return value * TimeUnit.MINUTES.toMillis(1L);
    case 's': 
      return value * TimeUnit.SECONDS.toMillis(1L);
    }
    return -1L;
  }
  
  public static class IntegerCountdown
  {
    public static String setFormat(Integer value)
    {
      int remainder = value.intValue() * 1000;
      
      int seconds = remainder / 1000 % 60;
      int minutes = remainder / 60000 % 60;
      int hours = remainder / 3600000 % 24;
      
      return (hours > 0 ? String.format("%02d:", new Object[] { Integer.valueOf(hours) }) : "") + String.format("%02d:%02d", new Object[] { Integer.valueOf(minutes), Integer.valueOf(seconds) });
    }
  }
  
  public static class LongCountdown
  {
    public static String setFormat(Long value)
    {
      if (value.longValue() < TimeUnit.MINUTES.toMillis(1L)) {
        return TimeUtils.getDecimalFormat().format(value.longValue() / 1000.0D) + "s";
      }
      return DurationFormatUtils.formatDuration(value.longValue(), (value.longValue() >= TimeUnit.HOURS.toMillis(1L) ? "HH:" : "") + "mm:ss");
    }
  }
}
