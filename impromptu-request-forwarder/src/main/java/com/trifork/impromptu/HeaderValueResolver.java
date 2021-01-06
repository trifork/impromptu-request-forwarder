package com.trifork.impromptu;

import java.util.Enumeration;
import java.util.List;

public interface HeaderValueResolver {
	List<String> getValue(String key);

    Enumeration<String> getKeys();
}
