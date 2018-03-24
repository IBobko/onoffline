package ru.todo100.activer.populators;

import ru.todo100.activer.data.DreamData;
import ru.todo100.activer.model.DreamItem;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>.
 */
public class DreamPopulator implements Populator<DreamItem,DreamData> {
    @Override
    public DreamData populate(final DreamItem dreamItem) {
        final DreamData dreamData = new DreamData();
        dreamData.setId(dreamItem.getId());
        dreamData.setPhoto(dreamItem.getPhoto());
        dreamData.setText(dreamItem.getName());
        return dreamData;
    }
}
