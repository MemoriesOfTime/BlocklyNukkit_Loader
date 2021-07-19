package com.blocklynukkit.loader.other;

public class FakeTiming extends AbstractTiming{
    @Override
    public long start() {
        return 0;
    }

    @Override
    public long end(long id) {
        return 0;
    }

    @Override
    public void finish(long id) {

    }

    @Override
    public void finish(String info) {

    }
}
