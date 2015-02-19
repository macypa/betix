package betix.core.config;

public enum Stake {
    stake1(1),
    stake2(2),
    stake3(3),
    stake4(4),
    stake5(5),
    stake6(6),
    stake7(7),
    stake8(8),
    stake9(9);

    private final Configuration stakesConfig = new Configuration(Configuration.STAKES_FILE);
    private boolean useFibonacci = stakesConfig.getConfigAsBoolean(ConfigKey.useFibonacciForStakes);
    private boolean useMartingale = stakesConfig.getConfigAsBoolean(ConfigKey.useMartingaleForStakes);
    private double minBetStake = stakesConfig.getConfigAsDouble(ConfigKey.minBetStake);

    private final int stakeCount;
    public final Double value;

    private Stake(int stakeCount) {
        this.stakeCount = stakeCount;
        this.value = getStake(stakeCount);
    }

    private Double getStake(int stakeCount) {

        if (useFibonacci) {
            return fibonacci(stakeCount);
        } else if (useMartingale) {
            return minBetStake * Math.pow(2, stakeCount - 1);
        } else {
            return stakesConfig.getConfigAsDouble(ConfigKey.valueOf("stake" + stakeCount));
        }
    }

    public double fibonacci(int number) {
        if (number == 1) {
            return minBetStake;
        }
        if (number == 2) {
            return minBetStake * 2;
        }

        return fibonacci(number - 1) + fibonacci(number - 2);
    }

    public Stake next() {
        switch (this) {
            case stake1:
                return stake2;
            case stake2:
                return stake3;
            case stake3:
                return stake4;
            case stake4:
                return stake5;
            case stake5:
                return stake6;
            case stake6:
                return stake7;
            case stake7:
                return stake8;
            case stake8:
                return stake9;
            case stake9:
                return stake1;
        }
        return stake1;
    }

    public static Stake get(double stake) {
        for (Stake s : Stake.values()) {
            if (s.value == stake) {
                return s;
            }
        }
        return stake1;
    }

    public static Stake get(String stake) {
        for (Stake s : Stake.values()) {
            if (s.toString().equals(stake)) {
                return s;
            }
        }
        return stake1;
    }

    @Override
    public String toString() {
        if (value % 1 == 0) {
            return String.valueOf(value.intValue());
        }
        return String.valueOf(value);
    }
}
