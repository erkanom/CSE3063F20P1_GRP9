import java.io.IOException;
public interface BotSelectionHelper {
	public void verifyAndLabel(User user, Label[] choosenLabels, Instance instance, WorkSpace workspace,boolean condition) throws IOException;
    public  Label[] randomLabelId(WorkSpace workSpace,Instance Instance);
}
