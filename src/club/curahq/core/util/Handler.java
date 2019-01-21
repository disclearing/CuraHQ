package club.curahq.core.util;

import club.curahq.core.Core;

public class Handler
{
  private Core plugin;
  
  public Handler(Core paramNotorious) {
    this.plugin = paramNotorious;
  }
  
  public void enable() {}
  
  public void disable() {}
  
  public Core getInstance() {
    return this.plugin;
  }
}
