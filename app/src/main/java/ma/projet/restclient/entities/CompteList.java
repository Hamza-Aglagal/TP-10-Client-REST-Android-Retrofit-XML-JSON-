package ma.projet.restclient.entities;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "List", strict = false)
public class CompteList {
    @ElementList(inline = true, entry = "item")
    private List<Compte> mItems;

    public List<Compte> getComptes() {
        return mItems;
    }

    public void setComptes(List<Compte> items) {
        this.mItems = items;
    }
}
