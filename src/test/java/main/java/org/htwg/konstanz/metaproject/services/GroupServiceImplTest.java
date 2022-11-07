package main.java.org.htwg.konstanz.metaproject.services;
/*
import main.java.org.htwg.konstanz.metaproject.entities.UserGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupServiceImplTest {

    private GroupServiceImpl underTest;

    @Before
    public void setUp() {
        underTest = new GroupServiceImpl(userDAO, groupDAO, metaprojectDAO, roleDAO, relationMetaprojectUserDao);
    }

    @Test
    public void hasCyclicReferences_no_cycle() {
        UserGroup child = new UserGroup();
        child.setId(1l);

        UserGroup parent = new UserGroup();
        parent.setId(2l);
        parent.getSubgroups().add(child);

        boolean hasCyclicReference = underTest.hasCyclicReferences(parent.getId(),parent);
        assertFalse(hasCyclicReference);
    }

    @Test
    public void hasCyclicReferences_direct_cycle(){
        UserGroup child = new UserGroup();
        child.setId(1l);

        UserGroup parent = new UserGroup();
        parent.setId(2l);
        parent.getSubgroups().add(child);

        //create cycle
        child.getSubgroups().add(parent);

        boolean hasCyclicReference= underTest.hasCyclicReferences(parent.getId(), parent);
        assertTrue(hasCyclicReference);
    }

    @Test
    public void hasCyclicReferences_indirect_cycle(){
        UserGroup child = new UserGroup();
        child.setId(1l);

        UserGroup parent = new UserGroup();
        parent.setId(2l);
        parent.getSubgroups().add(child);

        UserGroup grandParent = new UserGroup();
        grandParent.setId(3l);
        grandParent.getSubgroups().add(parent);

        //create cycle
        child.getSubgroups().add(grandParent);

        boolean hasCyclicReference= underTest.hasCyclicReferences(grandParent.getId(), grandParent);
        assertTrue(hasCyclicReference);
    }

    @Test
    public void getAllParents(){
        UserGroup g1 = new UserGroup();
        g1.setId(1l);

        UserGroup g2 = new UserGroup();
        g2.setId(2l);

        UserGroup g3 = new UserGroup();
        g3.setId(3l);
        g3.getParentGroups().add(g1);
        g1.getSubgroups().add(g3);

        UserGroup g4 = new UserGroup();
        g4.setId(4l);
        g4.getParentGroups().add(g1);
        g1.getSubgroups().add(g4);
        g4.getParentGroups().add(g2);
        g2.getSubgroups().add(g4);

        UserGroup g5 = new UserGroup();
        g5.setId(5l);
        g5.getParentGroups().add(g2);
        g2.getSubgroups().add(g5);

        UserGroup g6 = new UserGroup();
        g6.setId(6l);
        g6.getParentGroups().add(g3);
        g3.getSubgroups().add(g6);
        g6.getParentGroups().add(g4);
        g4.getSubgroups().add(g6);

        UserGroup g7 = new UserGroup();
        g7.setId(7l);
        g7.getParentGroups().add(g5);
        g5.getSubgroups().add(g7);


        List<UserGroup> parentsG6 = underTest.getAllParents(g6);
        UserGroup[] expectedG6 = {g3, g4, g1, g2};
        assertTrue(parentsG6.size() == expectedG6.length);
        assertTrue(parentsG6.containsAll(Arrays.asList(expectedG6)));

        List<UserGroup> parentsG7 = underTest.getAllParents(g7);
        UserGroup[] expectedG7 = {g5, g2};
        assertTrue(parentsG7.size() == expectedG7.length);
        assertTrue(parentsG7.containsAll(Arrays.asList(expectedG7)));
    }

}*/