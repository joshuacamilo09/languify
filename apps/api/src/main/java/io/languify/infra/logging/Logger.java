package io.languify.infra.logging;

public final class Logger {
  public static void info(org.slf4j.Logger logger, String message, Object... context) {
    if (logger.isInfoEnabled()) logger.info(format(message, context));
  }

  public static void warn(org.slf4j.Logger logger, String message, Object... context) {
    if (logger.isWarnEnabled()) logger.warn(format(message, context));
  }

  public static void error(org.slf4j.Logger logger, String message, Throwable throwable, Object... context) {
    if (logger.isErrorEnabled()) logger.error(format(message, context), throwable);
  }

  private static String format(String message, Object... context) {
    if (context == null || context.length == 0) return message;
    StringBuilder builder = new StringBuilder(message).append(" | ");

    for (int i = 0; i < context.length; i += 2) {
      Object key = context[i];
      Object value = i + 1 < context.length ? context[i + 1] : "";
      builder.append(key).append('=').append(value);

      if (i + 2 > context.length) builder.append(", ");;
    }

    return builder.toString();
  }
}
