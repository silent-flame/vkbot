package silentflame.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBotService;
import silentflame.bot.VkEventsProcessor;
import silentflame.bot.VkMessage;

import java.util.Set;

@Slf4j
@Component
public class HelpCommand implements Command {
  private final VkBotService vkBotService;
  private String keyword = "help";
  private String description = "Print this message";
  private final Set<Command> commands;

  public HelpCommand(VkBotService vkBotService, VkEventsProcessor vkEventsProcessor) {
    this.vkBotService = vkBotService;
    this.commands = vkEventsProcessor.getCommands();
    this.commands.add(this);
  }

  @Override
  public void execute(VkMessage message) {
    StringBuilder stringBuilder = new StringBuilder();
    commands.forEach(command -> stringBuilder.append(command.getKeyWord()).append("---").append(command.getDescription()).append("\n"));
    vkBotService.sendMessage(message.getFromId(), stringBuilder.toString());
  }

  @Override
  public String getKeyWord() {
    return keyword;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
