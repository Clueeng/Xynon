package fr.flaily.xynon.module.settings.impl;

import fr.flaily.xynon.module.settings.Setting;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class MultiSelectSetting extends Setting<List<String>> {

    private final List<String> options;

    private final List<String> selectedOptions;

    /**
     * Constructs a MultiSelectSetting with a given name, default selected options, a dependency, and a list of available options.
     *
     * @param name            The setting name.
     * @param defaultSelected The default list of selected options.
     * @param dependency      A supplier to check if the setting should be active.
     * @param options         The available options.
     */
    public MultiSelectSetting(String name, List<String> defaultSelected, Supplier<Boolean> dependency, String... options) {
        super(name, defaultSelected, dependency);
        this.name = name;
        this.options = Arrays.asList(options);
        this.selectedOptions = new ArrayList<>();
        for (String opt : defaultSelected) {
            if (this.options.contains(opt)) {
                this.selectedOptions.add(opt);
            }
        }
    }

    /**
     * Constructs a MultiSelectSetting with a given name, default selected options, and available options.
     *
     * @param name            The setting name.
     * @param defaultSelected The default list of selected options.
     * @param options         The available options.
     */
    public MultiSelectSetting(String name, List<String> defaultSelected, String... options) {
        this(name, defaultSelected, () -> true, options);
    }

    /**
     * Constructs a MultiSelectSetting with a given name, dependency, and available options.
     * The default selection will be empty.
     *
     * @param name       The setting name.
     * @param dependency A supplier to check if the setting should be active.
     * @param options    The available options.
     */
    public MultiSelectSetting(String name, Supplier<Boolean> dependency, String... options) {
        this(name, new ArrayList<>(), dependency, options);
    }

    /**
     * Constructs a MultiSelectSetting with a given name and available options.
     * The default selection will be empty.
     *
     * @param name    The setting name.
     * @param options The available options.
     */
    public MultiSelectSetting(String name, String... options) {
        this(name, new ArrayList<>(), () -> true, options);
    }

    /**
     * Toggles the selection state of an option. If the option is already selected,
     * it will be deselected; otherwise, it will be added to the selection.
     *
     * @param option The option to toggle.
     */
    public void toggle(String option) {
        // Find the actual option (ignoring case) from the options list
        for (String opt : options) {
            if (opt.equalsIgnoreCase(option)) {
                if (isSelected(opt)) {
                    // Remove the option ignoring case
                    selectedOptions.removeIf(selected -> selected.equalsIgnoreCase(opt));
                } else {
                    selectedOptions.add(opt);
                }
                break;
            }
        }
    }



    public Consumer<String> onChange;
    public void onValueChange(Consumer<String> consumer) {
        this.onChange = consumer;
    }

    /**
     * Checks if a given option is currently selected (non case sensitive).
     *
     * @param option The option to check.
     * @return true if the option is selected, false otherwise.
     */
    public boolean isSelected(String option) {
        for (String selected : selectedOptions) {
            if (selected.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Explicitly sets the selected options. Only valid options from the available list will be stored.
     *
     * @param newSelection The new list of selected options.
     */
    public void setSelectedOptions(List<String> newSelection) {
        selectedOptions.clear();
        for (String option : newSelection) {
            for (String opt : options) {
                if (opt.equalsIgnoreCase(option)) {
                    selectedOptions.add(opt);
                    break;
                }
            }
        }
    }
}
