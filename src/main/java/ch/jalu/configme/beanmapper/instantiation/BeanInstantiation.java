package ch.jalu.configme.beanmapper.instantiation;

import ch.jalu.configme.beanmapper.propertydescription.BeanPropertyDescription;
import ch.jalu.configme.properties.convertresult.ConvertErrorRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BeanInstantiation {

    @NotNull List<BeanPropertyDescription> getProperties();

    @Nullable Object create(@NotNull List<Object> propertyValues,
                            @NotNull ConvertErrorRecorder errorRecorder);

}
