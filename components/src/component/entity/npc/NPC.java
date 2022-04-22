package component.entity.npc;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@PooledWeaver
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NPC extends Component implements Serializable {

    private int id;

}
