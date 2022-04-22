package component.entity;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@PooledWeaver
@NoArgsConstructor
@AllArgsConstructor
public class Description extends Component implements Serializable {

    public String text;

}
