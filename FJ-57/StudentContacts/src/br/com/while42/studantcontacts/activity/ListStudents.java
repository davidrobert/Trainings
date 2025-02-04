package br.com.while42.studantcontacts.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.while42.studantcontacts.R;
import br.com.while42.studantcontacts.communication.Synchronism;
import br.com.while42.studantcontacts.model.Student;
import br.com.while42.studantcontacts.persist.StudentDAO;
import br.com.while42.studantcontacts.receiver.SMSReceiver;

public class ListStudents extends Activity {

	private Student studentSelected;
	private List<Student> students = new ArrayList<Student>();
	private ArrayAdapter<Student> adapter;
	private ListView listStudents;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem newStudent = menu.add(0, 0, 0, "Novo");
		MenuItem sync = menu.add(0, 1, 0, "Sicronizar");
		MenuItem gallery = menu.add(0, 2, 0, "Galeria");
		MenuItem map = menu.add(0, 3, 0, "Map");		

		newStudent.setIcon(R.drawable.novo);
		sync.setIcon(R.drawable.smile);
		gallery.setIcon(R.drawable.foto);
		map.setIcon(R.drawable.mapa);

		newStudent.setIntent(new Intent(this, FormStudent.class));
		gallery.setIntent(new Intent(ListStudents.this, GalleryStudents.class));
		
		//startActivity(new Intent(ListStudents.this, MapStudents.class));
		map.setIntent(new Intent(ListStudents.this, MapStudents.class));
		
		sync.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Synchronism sync = new Synchronism(ListStudents.this);
				sync.syncronize();
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);		

		menu.setHeaderTitle(studentSelected.toString());

		MenuItem call = menu.add(0, 0, 0, "Ligar");
		MenuItem sms = menu.add(0, 1, 0, "Enviar SMS");
		MenuItem map = menu.add(0, 2, 0, "Achar no Mapa");
		MenuItem site = menu.add(0, 3, 0, "Navegar no site");
		MenuItem del = menu.add(0, 4, 0, "Deletar");
		MenuItem email = menu.add(0, 5, 0, "Enviar E-mail");

		call.setOnMenuItemClickListener(new OnMenuItemClickListener() {			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent ligar = new Intent(Intent.ACTION_CALL);
				ligar.setData(Uri.parse("tel:" + studentSelected.getPhone()));
				startActivity(ligar);

				return true;
			}
		});

		sms.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				/*
				Intent sms = new Intent(Intent.ACTION_VIEW);
				sms.setData(Uri.parse("sms:" + studentSelected.getPhone()));
				sms.putExtra("sms_body", "Minha mensagem!");
				startActivity(sms);
				 */

				SmsManager smsManager = SmsManager.getDefault();
				PendingIntent sentIntent = PendingIntent.getActivity(ListStudents.this, 0, null, 0);

				if (PhoneNumberUtils.isWellFormedSmsAddress(studentSelected.getPhone())) {
					String text = "Primeiro SMS!";
					smsManager.sendTextMessage(studentSelected.getPhone(), null, text, sentIntent, null);
					Toast.makeText(ListStudents.this, "SMS Enviado", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ListStudents.this, "Telefone mal formatado", Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});

		map.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent map = new Intent(Intent.ACTION_VIEW);
				map.setData(Uri.parse("geo:0,0?z=14&q="+studentSelected.getAddress()));
				startActivity(map);
				return true;
			}
		});

		site.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				//				Intent web = new Intent(Intent.ACTION_VIEW);
				//				web.setData(Uri.parse(studentSelected.getSite()));
				//				startActivity(web);

				Intent edit = new Intent(ListStudents.this, WebStudent.class);
				edit.putExtra("alunoSelecionado", studentSelected);
				startActivity(edit);
				return true;
			}
		});

		del.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {


				Builder alert = new AlertDialog.Builder(ListStudents.this);
				alert.setTitle("Deseja realmente deletar?");
				alert.setPositiveButton("Sim", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						StudentDAO dao = new StudentDAO(ListStudents.this);
						dao.delete(studentSelected);
						students.remove(studentSelected);
						dao.close();

						adapter.notifyDataSetChanged();

					}
				});
				alert.setNegativeButton("Não", null);
				alert.show();

				return true;
			}
		});

		email.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent mail = new Intent(Intent.ACTION_SEND);
				mail.setType("message/rfc822");
				mail.putExtra(Intent.EXTRA_EMAIL, "");
				mail.putExtra(Intent.EXTRA_SUBJECT, "Assunto");
				mail.putExtra(Intent.EXTRA_TEXT, "Texto do email" );				

				startActivity(Intent.createChooser(mail, "Seleciona a aplicação de email"));
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		StudentDAO dao = new StudentDAO(this);
		students.clear();
		students.addAll(dao.getList());
		dao.close();

		adapter.notifyDataSetChanged();
		
		int idNotification = getIntent().getIntExtra(SMSReceiver.NOTIFICATION_LABEL, -1);
		if (idNotification != -1) {
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(idNotification);
		}		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista);

		listStudents = (ListView) findViewById(R.id_list.listagem);                                      
		int layout = android.R.layout.simple_list_item_1;

		// TODO; Transformar em uma classe qu herde de ArrayAdapter
		adapter = new ArrayAdapter<Student>(this, layout, students) {

			public int getCount() {			
				return students.size();
			}

			public long getItemId(int posicao) {
				return students.get(posicao).getId();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View item = ListStudents.this.getLayoutInflater().inflate(R.layout.student_item, null);

				ImageView photo = (ImageView) item.findViewById(R.id_student_item.imageViewPhoto);
				TextView name = (TextView) item.findViewById(R.id_student_item.textViewName);

				Student student = students.get(position);
				name.setText(student.getName());

				Bitmap bm;
				if (student.getPhoto() != null) {					
					bm = BitmapFactory.decodeFile(student.getPhoto());
				} else {					
					bm = BitmapFactory.decodeResource(ListStudents.this.getResources(), R.drawable.noimage);
				}

				bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
				photo.setImageBitmap(bm);

				return item;
			}

		};

		listStudents.setAdapter(adapter);
		listStudents.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int posicao, long id) {
				Toast.makeText(ListStudents.this, students.get(posicao).toString(), Toast.LENGTH_SHORT).show();				
			}

		});

		listStudents.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v,
					int posicao, long id) {

				Intent edit = new Intent(ListStudents.this, FormStudent.class);
				edit.putExtra("alunoSelecionado", students.get(posicao));
				startActivity(edit);

			}
		});

		listStudents.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View v,
					int posicao, long id) {
				studentSelected = students.get(posicao);
				registerForContextMenu(listStudents);

				//Toast.makeText(ListaAlunosActivity.this, "long", Toast.LENGTH_SHORT).show();

				return false; // se colocar true nao propaga o evento para outros listeners
			}
		});
	}
}