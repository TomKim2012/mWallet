package com.workpoint.mwallet.client.ui.transactions;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.workpoint.mwallet.client.service.TaskServiceCallback;
import com.workpoint.mwallet.client.ui.events.ProcessingCompletedEvent;
import com.workpoint.mwallet.client.ui.events.ProcessingEvent;
import com.workpoint.mwallet.client.ui.util.NumberUtils;
import com.workpoint.mwallet.shared.model.TransactionDTO;
import com.workpoint.mwallet.shared.requests.GetTransactionsRequest;
import com.workpoint.mwallet.shared.responses.GetTransactionsRequestResult;

public class TransactionsPresenter extends
		PresenterWidget<TransactionsPresenter.ITransactionView>
// implements ActivitySelectionChangedHandler, ProgramsReloadHandler,
// ResizeHandler
{

	@ContentSlot
	public static final Type<RevealContentHandler<?>> FILTER_SLOT = new Type<RevealContentHandler<?>>();

	// @Inject
	// FilterPresenter filterPresenter;

	public interface ITransactionView extends View {

		void presentData(TransactionDTO trxs);

		void setMiddleHeight();

		void clear();

		void presentSummary(String transactions, String amount);
	}

	@Inject
	DispatchAsync requestHelper;

	@Inject
	PlaceManager placeManager;

	Long programId;
	String programCode;

	@Inject
	public TransactionsPresenter(final EventBus eventBus,
			final ITransactionView view) {
		super(eventBus, view);
	}

	@Override
	protected void onReset() {
		super.onReset();
		getView().setMiddleHeight();
		loadData();
	}

	List<TransactionDTO> trxs = new ArrayList<TransactionDTO>();

	private void loadData() {
		fireEvent(new ProcessingEvent());
		requestHelper.execute(new GetTransactionsRequest(),
				new TaskServiceCallback<GetTransactionsRequestResult>() {
					@Override
					public void processResult(
							GetTransactionsRequestResult aResponse) {
						trxs = aResponse.getTransactions();
						bindTransactions();
						fireEvent(new ProcessingCompletedEvent());
					}
				});

	}

	protected void bindTransactions() {
		getView().clear();
		Double totalAmount = 0.0;
		for (TransactionDTO transaction : trxs) {
			totalAmount = totalAmount + transaction.getAmount();
			getView().presentData(transaction);
		}
		
		getView().presentSummary(
				NumberUtils.NUMBERFORMAT.format(trxs.size()),
				NumberUtils.CURRENCYFORMAT.format(totalAmount));
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

}