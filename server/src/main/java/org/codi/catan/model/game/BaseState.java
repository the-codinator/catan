/*
 * @author the-codinator
 * created on 2020/5/26
 */

package org.codi.catan.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.EnumMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codi.catan.model.core.IdentifiableEntity;
import org.codi.catan.model.core.StrongEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseState implements IdentifiableEntity, StrongEntity {

    public BaseState(String id) {
        this.id = id;
    }

    private String id;
    private Phase phase;
    private List<House> houses;
    private List<Road> roads;
    private int thief;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Resource, Integer> bank;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<Color, List<DevCard>> playedDevCards;
    @JsonInclude(Include.NON_NULL)
    private EnumMap<AchievementType, AchievementValue> achievements;
    private CurrentMove currentMove;
    @SuppressWarnings("checkstyle:MemberName")
    private String eTag;

    @Override
    @JsonIgnore
    public String getETag() {
        return this.eTag;
    }
}
