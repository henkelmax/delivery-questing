package de.maxhenkel.delivery.integration.patchouli;

import de.maxhenkel.delivery.Main;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ConfigProcessor implements IComponentProcessor {

    private IVariable text;

    @Override
    public void setup(IVariableProvider provider) {
        text = provider.get("text");
    }

    @Override
    public IVariable process(String key) {
        if (key.equals("text")) {
            return IVariable.wrap(text.asString().replace("#min_computer_level#", Main.SERVER_CONFIG.minComputerLevel.get().toString()));
        }
        return null;
    }
}
