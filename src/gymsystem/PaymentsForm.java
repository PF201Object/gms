package gymsystem;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PaymentsForm extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    
    public PaymentsForm() {
        initComponents(); // MUST BE FIRST
        loadPaymentsTable();
    }   
    
	private void loadPaymentsTable() {
		String[] columns = {"Payment ID", "Member ID", "Member Name", "Amount", "Payment Date", "Payment Type", "Month", "Status"};
		
		// Initialize the model
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		// Check if table exists before setting model
		if (tblPayments != null) {
			tblPayments.setModel(tableModel);
		} else {
			System.err.println("Error: tblPayments is null!");
			return;
		}
		
		try {
			List<String[]> payments = DatabaseHelper.getAllPayments();
			if (payments != null) {
				for (String[] payment : payments) {
					tableModel.addRow(payment);
				}
			}
		} catch (Exception e) {
			System.err.println("Database Error: " + e.getMessage());
		}
	}

    public void refreshTable() {
        loadPaymentsTable();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    
    // --- CRITICAL FIX: Initialize the table object ---
    tblPayments = new javax.swing.JTable(); 
    
    btnAddPayment = new javax.swing.JButton();
    btnMarkPaid = new javax.swing.JButton();
    btnRefresh = new javax.swing.JButton();
    btnClose = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Payment Management");

    // Header Panel Setup
    jPanel1.setBackground(new java.awt.Color(204, 102, 255));
    jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); 
    jLabel1.setForeground(new java.awt.Color(255, 255, 255));
    jLabel1.setText("PAYMENT MANAGEMENT");
    jPanel1.add(jLabel1);

    // --- CRITICAL FIX: Connect the table to the ScrollPane ---
    jScrollPane1.setViewportView(tblPayments);

    // Button Setup
    btnAddPayment.setBackground(new java.awt.Color(46, 204, 113));
    btnAddPayment.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
    btnAddPayment.setForeground(new java.awt.Color(255, 255, 255));
    btnAddPayment.setText("ADD PAYMENT");
    btnAddPayment.addActionListener(evt -> btnAddPaymentActionPerformed(evt));

    btnMarkPaid.setBackground(new java.awt.Color(52, 152, 219));
    btnMarkPaid.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
    btnMarkPaid.setForeground(new java.awt.Color(255, 255, 255));
    btnMarkPaid.setText("MARK AS PAID");
    btnMarkPaid.addActionListener(evt -> btnMarkPaidActionPerformed(evt));

    btnRefresh.setBackground(new java.awt.Color(230, 126, 34));
    btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
    btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
    btnRefresh.setText("REFRESH");
    btnRefresh.addActionListener(evt -> btnRefreshActionPerformed(evt));

    btnClose.setBackground(new java.awt.Color(149, 165, 166));
    btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); 
    btnClose.setForeground(new java.awt.Color(255, 255, 255));
    btnClose.setText("CLOSE");
    btnClose.addActionListener(evt -> btnCloseActionPerformed(evt));

    // Layout Management
    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnAddPayment)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnMarkPaid)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnRefresh)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClose)))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAddPayment)
                .addComponent(btnMarkPaid)
                .addComponent(btnRefresh)
                .addComponent(btnClose))
            .addContainerGap())
    );

    pack();
}// </editor-fold>//GEN-END:initComponents

    private void btnAddPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPaymentActionPerformed
        // Assuming AddPaymentForm is a separate JDialog class or similar
        // For now, keeping your logic of calling a form.
    }//GEN-LAST:event_btnAddPaymentActionPerformed

    private void btnMarkPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkPaidActionPerformed
        int selectedRow = tblPayments.getSelectedRow();
        if (selectedRow >= 0) {
            String memberName = tableModel.getValueAt(selectedRow, 2).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Mark payment for " + memberName + " as PAID?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.setValueAt("PAID", selectedRow, 7);
                JOptionPane.showMessageDialog(this, "Payment marked as PAID!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a payment first!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnMarkPaidActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadPaymentsTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddPayment;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnMarkPaid;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPayments;
    // End of variables declaration//GEN-END:variables
}