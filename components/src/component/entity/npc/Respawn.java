package component.entity.npc;

import com.artemis.Component;
import com.artemis.annotations.All;
import com.artemis.annotations.PooledWeaver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@PooledWeaver
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Respawn extends Component implements Serializable {

    private float time;
    private int npcId;
    private OriginPos pos;
}
