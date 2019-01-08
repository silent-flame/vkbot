package silentflame.bot.commands;

import silentflame.bot.VkMessage;

import java.util.Set;

public interface Command {
  void execute(VkMessage message);

  String getKeyWord();

  String getDescription();
}