package component.entity.character;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@PooledWeaver
@NoArgsConstructor
public class Character extends Component implements Serializable {

}
