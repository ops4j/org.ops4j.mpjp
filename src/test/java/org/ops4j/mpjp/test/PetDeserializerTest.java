package org.ops4j.mpjp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.junit.jupiter.api.Test;
import org.ops4j.mpjp.test.model.Carrier;
import org.ops4j.mpjp.test.model.Cat;
import org.ops4j.mpjp.test.model.Dog;
import org.ops4j.mpjp.test.model.Pet;
import org.ops4j.mpjp.test.model.PetDeserializer;
import org.ops4j.mpjp.test.model.PetSerializer;
import org.ops4j.mpjp.test.model.Carrier.CarrierType;

public class PetDeserializerTest {
	
	@Test
	public void shouldDeserializePets() {
		List<Carrier<Pet>> carriers = new ArrayList<>();
		
		Cat harris = new Cat();
		harris.setAge(10);
		harris.setFurry(true);
		harris.setCuddly(true);
		
		Dog falco = new Dog();
		falco.setAge(4);
		falco.setFurry(false);
		falco.setBarking(false);
		carriers.add(new Carrier<>(CarrierType.BAG, harris));
		carriers.add(new Carrier<>(CarrierType.CRATE, falco));
		
		@SuppressWarnings("serial")
		Type carrierListType = new ArrayList<Carrier<Pet>>() {}.getClass().getGenericSuperclass();

		JsonbConfig config = new JsonbConfig()
		        .withFormatting(true)
		        .withSerializers(new PetSerializer())
		        .withDeserializers(new PetDeserializer());

		Jsonb jsonb = JsonbBuilder.create(config);

		String json = jsonb.toJson(carriers, carrierListType);

		List<Carrier<Pet>> list = jsonb.fromJson(json, carrierListType);
		assertThat(list).hasSize(2);		
		assertThat(list.get(0).getCarrierType()).isEqualTo(CarrierType.BAG);
		assertThat(list.get(0).getCarriedPet()).isInstanceOf(Cat.class);
		assertThat(list.get(1).getCarrierType()).isEqualTo(CarrierType.CRATE);
		assertThat(list.get(1).getCarriedPet()).isInstanceOf(Dog.class);
		
		Cat cat = (Cat) list.get(0).getCarriedPet();
		assertThat(cat.getCuddly()).isTrue();
		Dog dog = (Dog) list.get(1).getCarriedPet();
		assertThat(dog.getBarking()).isFalse();
	}
}
