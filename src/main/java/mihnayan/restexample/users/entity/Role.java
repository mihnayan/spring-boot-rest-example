package mihnayan.restexample.users.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Roles
 */
@Entity
@Table(name="roles")
public class Role {

    @Id
    private int id;

    @NotNull
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
