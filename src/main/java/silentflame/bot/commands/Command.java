package silentflame.bot.commands;

import silentflame.bot.VkMessage;

public interface Command {
  void execute(VkMessage message);

  String getKeyWord();

  String getDescription();
}