package me.quesia.peepopractice.core.category;

import com.google.gson.JsonObject;
import me.quesia.peepopractice.PeepoPractice;
import me.quesia.peepopractice.core.PracticeWriter;
import me.quesia.peepopractice.core.category.utils.PracticeCategoryUtils;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryPreference {
    private String id;
    private String label;
    private String description;
    private List<String> choices = new ArrayList<>();
    private String defaultChoice;
    private Identifier icon;

    public String getId() {
        return this.id;
    }

    public CategoryPreference setId(String id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        if (this.label == null && this.id != null) {
            return PracticeCategoryUtils.getNameFromId(this.id);
        }
        return this.label;
    }

    public CategoryPreference setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public CategoryPreference setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getChoices() {
        return this.choices;
    }

    public CategoryPreference setChoices(List<String> choices) {
        this.choices = choices;
        return this;
    }

    public CategoryPreference setChoices(String[] choices) {
        this.choices.clear();
        this.choices.addAll(List.of(choices));
        return this;
    }

    public String getDefaultChoice() {
        return this.defaultChoice;
    }

    public CategoryPreference setDefaultChoice(String defaultChoice) {
        this.defaultChoice = defaultChoice;
        return this;
    }

    public Identifier getIcon() {
        return this.icon;
    }

    public CategoryPreference setIcon(Identifier icon) {
        this.icon = icon;
        return this;
    }

    public static int getIndex(String value, List<String> choices) {
        int index = 0;

        for (String choice : choices) {
            if (Objects.equals(value, choice)) {
                break;
            }
            index++;
        }

        return index;
    }

    public static CategoryPreference getPreferenceById(PracticeCategory category, String id) {
        for (CategoryPreference preference : category.getPreferences()) {
            if (preference.id.equals(id)) {
                return preference;
            }
        }
        return null;
    }

    public static boolean getBoolValue(String id) {
        return getBoolValue(PeepoPractice.CATEGORY, id);
    }

    public static boolean getBoolValue(PracticeCategory category, String id) {
        String value = getValue(category, id);
        if (value != null) {
            return PracticeCategoryUtils.parseBoolean(value);
        }
        return false;
    }

    public static String getValue(String id) {
        return getValue(PeepoPractice.CATEGORY, id);
    }

    public static String getValue(PracticeCategory category, String id, String def) {
        String value = getValue(category, id);
        if (value == null) {
            setValue(category, id, def);
            return def;
        }
        return value;
    }

    public static String getValue(PracticeCategory category, String id) {
        PracticeWriter writer = PracticeWriter.PREFERENCES_WRITER;
        JsonObject config = writer.get();

        JsonObject categoryObject = new JsonObject();
        if (config.has(category.getId())) {
            categoryObject = config.get(category.getId()).getAsJsonObject();
        }

        CategoryPreference categoryPreference = getPreferenceById(category, id);

        if (categoryPreference != null) {
            if (!categoryObject.has(id) || !categoryPreference.getChoices().contains(categoryObject.get(id).getAsString())) {
                return categoryPreference.getDefaultChoice();
            }
        }

        try { return categoryObject.get(id).getAsString(); }
        catch (NullPointerException ignored) { return null; }
    }

    public static String getValue(PracticeCategory category, CategoryPreference categoryPreference) {
        PracticeWriter writer = PracticeWriter.PREFERENCES_WRITER;
        JsonObject config = writer.get();

        JsonObject categoryObject = new JsonObject();
        if (config.has(category.getId())) {
            categoryObject = config.get(category.getId()).getAsJsonObject();
        }

        String id = categoryPreference.getId();
        try {
            if (!categoryObject.has(id) || !categoryPreference.getChoices().contains(categoryObject.get(id).getAsString())) {
                setValue(category, id, categoryPreference.getDefaultChoice());
                return categoryPreference.getDefaultChoice();
            }
        } catch (NullPointerException ignored) {}

        try { return categoryObject.get(id).getAsString(); }
        catch (NullPointerException ignored) { return null; }
    }

    public static void setValue(PracticeCategory category, String id, String value) {
        JsonObject config = PracticeWriter.PREFERENCES_WRITER.get();
        JsonObject categoryPreference = new JsonObject();
        if (config.has(category.getId())) {
            categoryPreference = config.get(category.getId()).getAsJsonObject();
        }
        categoryPreference.addProperty(id, value);
        PracticeWriter.PREFERENCES_WRITER.put(category.getId(), categoryPreference);
    }
}
