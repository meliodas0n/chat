package com.example.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat.databinding.ActivityMainBinding;

import org.jetbrains.annotations.Nullable;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

import static java.util.Collections.singletonList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // step 0 - inflate binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // step 1 - set up the client for API calls and the domain for offline storage
        ChatClient client = new ChatClient.Builder("b67pax5b2wdq", getApplicationContext()).build();
        new ChatDomain.Builder(client, getApplicationContext()).build();

        //step 2 - authenticate and connect the user
        User user = new User();
        user.setId("tutorial-droid");
        user.getExtraData().put("name", "Tutorial Droid");
        user.getExtraData().put("image", "https://bit.ly/2TIt8NR");

        client.connectUser(user, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.NhEr0hP9W9nwqV7ZkdShxvi02C5PR7SJE7Cs4y7kyqg").enqueue();

        //step 3 - set the channel list filter and order
        //this can be read as requiring only channels whose "type" is "messaging" AND
        //whose "member" include our "user.id"
        FilterObject filter = Filters.and(Filters.eq("type", "messaging"), Filters.in("members", singletonList(user.getId())));

        ChannelListViewModelFactory factory = new ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT);

        ChannelListViewModel channelsViewModel = new ViewModelProvider(this, factory).get(ChannelListViewModel.class);

        //step 4 - connect the channellistviewmodel to the channellistview, loose coupling makes it easy to customize
        ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, this);
        binding.channelListView.setChannelItemClickListener(channel -> startActivity(ChannelActivity.newIntent(this, channel)));
    }
}